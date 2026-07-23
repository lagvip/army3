# Chicken LT Agent Rules

## Package Manager
- Use the Gradle wrapper with Java 21.
- Compile: `.\gradlew.bat compileJava`
- Run locally: `.\gradlew.bat run`
- Check dependencies: `.\gradlew.bat dependencies`

## File-Scoped Commands
- No file-scoped Java checker is configured; use `rg -n "<pattern>" <file>` for inspection and `.\gradlew.bat compileJava` after Java edits.

## Mandatory Security Model
- Treat the client, every packet, and every client-visible value as untrusted.
- The client sends intent only; the server validates it and calculates the result.
- Derive player identity from the authenticated session, never from a packet field.
- Derive position, weapon, bullet type/count, stats, inventory, prices, rewards, wind, trajectory, collision, damage, HP, energy, cooldowns, and turn state from server state.
- Never accept client claims such as damage dealt, target hit, currency balance, item ownership, reward eligibility, or final coordinates.
- Client prediction may improve visuals, but it never changes authoritative server state without validation.

## Packet and Session Validation
- Allow each command only in its valid state: connected, authenticated, lobby, room, battle, or training.
- Before reading a packet, validate its exact/minimum length; validate all indexes, enums, ranges, string lengths, IDs, and trailing bytes.
- Check authentication, authorization, ownership, current room/match, alive state, current turn, cooldown, resource cost, and action quota.
- Reject impossible movement using prior server position, elapsed time, allowed distance, map bounds, and collision.
- A shot packet must not overwrite server position or select an unequipped weapon/bullet.
- Give turns and valuable actions server-issued sequence/action IDs; process each ID at most once.
- Rate-limit by connection, account, IP, and command; disconnect repeated malformed or abusive traffic without crashing or log flooding.

## Economy and Persistence
- Obtain shop prices and rewards from server configuration/templates only.
- Keep currency and quantities non-negative and enforce inventory capacity and ownership.
- Purchases, sales, rewards, upgrades, and transfers must be atomic database transactions with rollback on failure.
- Make valuable operations idempotent so reconnects, retries, and duplicate packets cannot duplicate value.
- Use prepared statements and database constraints; never build SQL from client text.

## Accounts and Network
- Store passwords with Argon2id or bcrypt plus a unique salt; never plaintext or reversible encryption.
- Do not add login bypasses, shared/default passwords, hidden admin commands, or client-side authorization.
- Limit login attempts and session creation; rotate/invalidate sessions on logout and reconnect.
- Production traffic containing credentials or game state must use TLS; custom XOR/packet keys are not encryption.
- Keep MariaDB private, expose only required game ports, and keep secrets outside source control.

## Logging and Verification
- Log authentication failures, invalid state transitions, impossible movement, duplicate actions, economy changes, and repeated validation failures.
- Never log passwords, session keys, database secrets, or complete sensitive packet contents.
- Before completion, test malformed/truncated packets, negative and maximum values, invalid indexes, repeated actions, wrong-turn shots, teleport attempts, insufficient currency, full inventory, reconnects, and database failure.
- Preserve invariants: one action per issued ID, reachable positions, server-derived combat, no negative balances, conserved items/currency, and rewards granted once.
- Compile after Java edits and report what was validated, what remains untested, and any security trade-off.

## Change Discipline
- Inspect the full request path before editing: packet decoder, router, session state, game logic, persistence, and response.
- Do not weaken server validation merely to make one client build work; adapt protocol compatibility without trusting client claims.
- Preserve unrelated user changes and avoid broad rewrites unless explicitly requested.

## Commit Attribution
- AI commits must include `Co-Authored-By: Codex <noreply@openai.com>`.
