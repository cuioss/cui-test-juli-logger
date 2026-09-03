---
name: release
description: Cut a cui-test-juli-logger release — bump .github/project.yml version, open and merge the release PR, wait for the automated Release workflow, verify the release landed, then reformat the generated GitHub release notes
user-invocable: true
allowed-tools: Bash, Read, Edit, AskUserQuestion
---

# Release Skill

Cuts a new cui-test-juli-logger release end-to-end: determine the version, open the
version-bump PR that triggers the release, merge it, wait for the automated Release
workflow, verify the release landed, and reformat the auto-generated GitHub release notes.

## How the release is wired (read first)

The release is **fully automated by GitHub Actions**. `.github/workflows/release.yml`
triggers on a **merged pull request that changes `.github/project.yml`**:

```yaml
on:
  pull_request:
    types: [closed]
    paths:
      - '.github/project.yml'
```

So this skill never runs Maven release goals by hand. Its job is to produce and merge the
correct `project.yml` change; the reusable `cuioss-organization` release workflow
(`reusable-maven-release.yml`) does the tagging, Maven Central deploy, GitHub release
creation, and — because `pages.deploy-at-release: true` — the documentation pages deploy.

Observed timings (use these as the basis for the waits below):
- PR gating check: **Maven Build ~3–6 min** (matrix over Java 21 + 25). This is a small
  test-support library with no integration/e2e suites, so a full green PR is typically
  **~4–7 min**.
- Release workflow itself: **~6 min**, but Maven Central propagation, the GitHub release
  publish, and the pages deploy can lag → allow **up to ~30 min** before treating it as
  stuck.

### This library is consumed by other repos

`project.yml` declares:

```yaml
consumers:
  - cuioss-parent-pom:version.cui.test.juli.logger
  - nifi-extensions:version.cui.test.juli.logger
```

After the release lands, the org automation opens parent-bump PRs in those repos. That is
follow-on automation, **not** part of this skill — but mention it in the final report so
the operator knows to expect them. A `cuioss-parent-pom` bump additionally needs its own
release before the wider org picks the change up.

## Workflow

### Step 1 — Determine the version number

`.github/project.yml` is the single source of truth for both versions — read it, never
assume:

```bash
grep -E 'current-version|next-version' .github/project.yml
```

- `release.current-version` — the **last released** version.
- `release.next-version` — what `pom.xml` carries between releases.

**Default rule:** the release version is `next-version` with `-SNAPSHOT` stripped. The new
`next-version` is the next minor bump plus `-SNAPSHOT`.

This project has shipped both shapes — two-segment minors and three-segment patches off the
same minor floor. For a **patch** release, `current-version` gains a patch segment and
`next-version` is left **unchanged**. Mirror the existing scheme rather than forcing a `.0`.

**Ask the user** (AskUserQuestion) when the choice is genuinely open — e.g. `current-version`
does not sit on the `next-version` line, so both the stripped `next-version` and the
incremented `current-version` are defensible, or the numbers look inconsistent. Otherwise
state the determined version and proceed.

### Step 2 — Check for open PRs

```bash
gh pr list --repo cuioss/cui-test-juli-logger --state open --json number,title,isDraft
```
- **No open PRs** → good, proceed.
- **Open PRs exist** → these would normally be merged before a release. Surface the list
  and **ask the user** whether to proceed anyway or wait. Do not silently ignore them.

Also confirm the working tree is clean (`git status --porcelain`) before branching.

### Step 3 — Pull current main

```bash
git checkout main && git pull --ff-only origin main
```

### Step 4 — Create the release branch

Branch name uses the `chore/` prefix (required — the Maven CI workflow only triggers on
`main`, `feature/*`, `fix/*`, `chore/*`, `release/*`, `dependabot/**`; other prefixes skip
the `build` check and block the merge):

```bash
git checkout -b chore/release_<version>
```

### Step 5 — Update `.github/project.yml`

Edit the `release` block:
- `current-version:` → the version determined in Step 1
- `next-version:` → next minor + `-SNAPSHOT`; **leave unchanged** for
  a patch release.

Leave everything else untouched. The README badges (CI, Maven Central, SonarCloud) are all
dynamic endpoints — there is **no** per-release badge to hand-edit.

### Step 6 — Commit, push, open PR

```bash
git add .github/project.yml
git commit -m "chore(release): prepare release <version>"
git push -u origin chore/release_<version>
# Create the label only when missing, and let a real failure (permissions, outage)
# surface - swallowing it would make `gh pr create --label` fail with a confusing error.
if ! gh label list --repo cuioss/cui-test-juli-logger --search skip-bot-review --json name --jq '.[].name' | grep -qx skip-bot-review; then
  gh label create skip-bot-review --repo cuioss/cui-test-juli-logger --description "Skip automated bot review" --color ededed
fi
gh pr create --repo cuioss/cui-test-juli-logger --base main \
  --title "chore(release): prepare release <version>" \
  --label "skip-bot-review" \
  --body "Bump current-version to <version>, next-version to <next>-SNAPSHOT. Triggers the automated Release workflow on merge."
```

The mechanical release PR carries the `skip-bot-review` label to skip automated bot review.

Use the project commit convention: `Co-Authored-By: Claude <noreply@anthropic.com>` (no
model name / no "Generated with Claude Code" footer).

### Step 7 — Wait for PR checks (~4–7 min)

Watch the checks rather than blindly sleeping:

```bash
gh pr checks <pr#> --repo cuioss/cui-test-juli-logger --watch
```

### Step 8 — Handle review comments / failures (if any)

- If a check fails, read the failing run's log (`gh run view <id> --log-failed`), fix the
  cause on the branch, push, and re-wait. **Never** merge a red PR.
- Follow the PR-comment protocol in `CLAUDE.md`; every comment MUST get a reply and MUST be
  resolved — fix it and say so, or explain why not. Ask the user when uncertain. Enumerate
  **all** comments, not just the first page:
  ```bash
  gh api --paginate repos/cuioss/cui-test-juli-logger/pulls/<pr#>/comments
  ```
  A single unhandled comment beyond the first page still blocks the merge, so never work
  from a truncated list.
- **Unresolved review threads block the merge.** A PR can show every check green and still
  report `BLOCKED` purely because a bot review thread is open; resolving the threads clears
  it. Do not misread that state as a branch-protection or approval problem. Resolve with:
  ```bash
  gh api graphql -f query='mutation{resolveReviewThread(input:{threadId:"<id>"}){thread{isResolved}}}'
  ```
  Thread ids come from (paginate until `hasNextPage` is false — do not assume one page
  covers every thread):
  ```bash
  gh api graphql --paginate -f query='query($endCursor:String){repository(owner:"cuioss",name:"cui-test-juli-logger"){pullRequest(number:<pr#>){reviewThreads(first:100,after:$endCursor){pageInfo{hasNextPage endCursor} nodes{id isResolved}}}}}'
  ```
  Before merging, assert the unresolved count is zero rather than eyeballing the list.
- Re-run Step 7 after any push.

### Step 9 — Merge → release starts automatically

Once checks are green and comments resolved:

```bash
gh pr merge <pr#> --repo cuioss/cui-test-juli-logger --squash --delete-branch
```
Merging this PR (it touches `.github/project.yml`) fires `release.yml` automatically — do
**not** dispatch the release manually unless the auto-trigger demonstrably did not fire.

### Step 10 — Wait for the Release workflow (~30 min)

```bash
gh run list --repo cuioss/cui-test-juli-logger --workflow "Release" --limit 3 \
  --json status,conclusion,displayTitle,databaseId
gh run watch <databaseId> --repo cuioss/cui-test-juli-logger
```
The workflow itself runs ~6 min; allow up to ~30 min for tag + GitHub release publish +
Maven Central propagation + pages deploy before treating it as stuck.

### Step 11 — Verify the release landed

```bash
gh release view <version> --repo cuioss/cui-test-juli-logger \
  --json tagName,name,isDraft,createdAt,body
git fetch --tags && git tag --list <version>
curl -sfI https://repo1.maven.org/maven2/de/cuioss/test/cui-test-juli-logger/<version>/cui-test-juli-logger-<version>.pom \
  -o /dev/null -w '%{http_code}\n'
```
Confirm the tag exists, a GitHub release for `<version>` was created, and the artifact is
resolvable from Maven Central (`200`).

**Check `isDraft`.** This repo has left a release sitting as a **draft** before — a draft
release is not visible to consumers and means the workflow did not finish publishing.
If `isDraft` is `true`, publish it (`gh release edit <version> --draft=false`) and
investigate the Release workflow run before reporting success.

### Step 12 — Reformat the generated release notes

The Release workflow creates the GitHub release with **auto-generated** notes (a flat
`## What's Changed` list). Rewrite them in place using the **house format below**, then
push the update:

```bash
mkdir -p .plan/temp
gh release view <version> --repo cuioss/cui-test-juli-logger --json body --jq .body > .plan/temp/release-<version>-orig.md
# ...build the reformatted body in .plan/temp/release-<version>.md...
gh release edit <version> --repo cuioss/cui-test-juli-logger --notes-file .plan/temp/release-<version>.md
```

#### House format rules (apply exactly)

1. **Two top-level groups:** `## Features & Enhancements` and `## Dependency Updates`.
2. **Features & Enhancements** — group functional PRs by theme with `###` subheadings,
   adapted to this library's domain, e.g.:
   - `### Assertions` — `LogAsserts`, record resolution, failure messages
   - `### Test Lifecycle` — `TestLoggerFactory`, `TestLoggerController`, `@EnableTestLogger`,
     handler install/uninstall semantics
   - `### Configuration` — `TestLogLevel`, `cui_logger.properties`, root/per-class levels
   - `### API & Code Quality` — public-API changes, refactors, cleanup
   - `### Documentation`
   Adapt theme headings to the actual PRs; omit empty sections.
3. **Dependency Updates** — group by type with `###` subheadings (this project is Java-only
   — there is no JavaScript group):
   - `### Java` — Java libraries (e.g. lombok, junit, cui-java-tools)
   - `### Infra` — platform/build/CI: build plugins, `cuioss-organization` workflow bumps,
     parent-POM / `cui-java-parent` updates
4. **Collapse by library identity — one line per library, spanning the full range.**
   The unit of collapsing is the *library*, not the PR title. Merge into a single line
   whenever the PRs concern the same library, in all three shapes that occur:
   - **Version chains** — several bumps of one artifact (`A → B → C`) collapse to one line
     spanning `A → C`, carrying the latest PR's author.
   - **The same library in several places** — one library bumped in more than one module or
     directory is **one** line naming them all, not one line each. Those titles differ only
     by that suffix, so do not wait for identical titles before merging.
   - **One upstream release landing as several coordinates** — when a single upstream bump
     arrives as separate PRs against different coordinates (e.g. a version property *and*
     a BOM or parent), that is **one** bump: one line naming the coordinates in parentheses.

   Carry every merged PR's URL onto the surviving line, comma-separated.
5. **Recover versions the title omits.** Dependabot truncates a title to
   `bump <lib> in /<dir>`, with no versions, when several dependencies must move together.
   Never publish a dependency line without a version range: read the PR body, which states
   ``Updates `<lib>` from X to Y``, and use those versions when computing the range:

   ```bash
   gh pr view <n> --repo cuioss/cui-test-juli-logger --json body --jq .body | head -6
   ```
6. **Remove all OpenRewrite bumps and friends** — drop every `rewrite-maven-plugin`,
   `rewrite-migrate-java`, `rewrite-testing-frameworks`, and related OpenRewrite dependency PR.
7. **Remove internal tooling churn** — drop PRs that only touch dev/build orchestration with
   no user-facing effect: `marshal.json`/plan-marshall config migrations, plan-marshall build
   wiring, internal dev-skill changes, and the mechanical version-bump PR itself.
8. **Preserve each kept PR line** in its original
   `* <title> by @author in <url>` shape. Rules 4 and 5 **override** verbatimness where
   they conflict: rewrite the title's version range to span the collapsed chain, and name
   the several modules or coordinates on the surviving line.
9. Keep the trailing `**Full Changelog**: ...compare/<prev>...<version>` line.

#### Verify before publishing (mandatory)

These rules are easy to under-apply: a duplicate survives whenever two PRs touch the same
library under differing titles. After building the notes file and **before**
`gh release edit`, assert that every library appears exactly once:

```bash
grep -oE '(bump|update) [^ ]+ (from|in)' .plan/temp/release-<version>.md \
  | sort | uniq -c | sort -rn | head
```

Every count must be `1`. Any count `>1` is an unmerged duplicate — collapse it per rule
4 and re-run. Also confirm that no dependency line is missing a version range
(rule 5).


### Step 13 — Done

Report: released version, release URL, the PR number, a short summary of how many dependency
PRs were collapsed/removed during note reformatting, and a reminder that consumer-bump PRs
will appear in `cuioss-parent-pom` and `nifi-extensions`.

## Critical rules

- The release is triggered by **merging a `.github/project.yml` change** — never hand-run
  Maven release goals.
- Branch prefix **must** be `chore/` (or another CI-accepted prefix) or the build check skips
  and the merge is blocked.
- Never merge a red PR; fix and re-wait.
- Every review comment gets a reply **and** gets resolved — unresolved threads block the merge.
- Verify the published release is **not a draft**.
- Temporary files go under `.plan/temp/`.
- Commit trailer: `Co-Authored-By: Claude <noreply@anthropic.com>`; no PR footer line.
