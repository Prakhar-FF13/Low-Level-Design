# Cap Table Generator — Design Guide (Interview-Oriented)

> **Implemented solution & API:** [README.md](./README.md)

This guide is the **interview narrative**: what to clarify, how to order events, how splits interact with issuances, and a sensible **implementation order** for a 1–1.5 hour live coding slot.

---

## Phase 1: Clarify & Scope

| Topic | Suggested assumption |
|--------|----------------------|
| Persistence / REST | Out of scope — **in-memory** replay from a `List` of transactions. |
| Stakeholder identity | **`String` name** (trimmed); ignore blank names. |
| Share counts | **`long`**; use **`multiplyExact` / `addExact`** on overflow. |
| Money | **`BigDecimal`** for valuation and derived value; fixed scales for divide/round (implementation detail). |
| Multiple classes of stock | Out of scope — one **common share** class. |
| Buybacks / transfers | Out of scope unless spec adds them — only **issuance**, **split**, **valuation** here. |
| Same-day event order | **Split → issuance → valuation** (given in spec). |

---

## Phase 2: Core entities (whiteboard)

**Inputs (transactions):**

- **ShareIssuance** — `date`, `stakeholderName`, `sharesIssued`
- **StockSplit** — `date`, `factor` (integer \(> 1\) when valid)
- **CompanyValuation** — `date`, `totalValuation`

**Working state while replaying:**

- `Map<StakeholderName, Long>` — current **outstanding** shares.
- **Latest valuation** — last **valid** `totalValuation` seen in chronological order (after same-day ordering).

**Output:**

- Per stakeholder: `name`, `shares`, `ownershipPercent`, optional `value`.

---

## Phase 3: Ordering & replay algorithm

1. **Filter** to `transaction.date <= asOfDate` (inclusive).
2. **Sort** by `date`, then by **type order** for ties: split (0), issuance (1), valuation (2).
3. **Single pass** over sorted list:
   - **Split:** for each map entry, `shares *= factor` (skip if `factor <= 1`).
   - **Issuance:** `map.merge(name, issued, addExact)` (skip invalid rows).
   - **Valuation:** replace “latest valuation” if the amount is valid (e.g. positive).
4. After the pass:
   - `totalOutstanding` = sum of map values.
   - If latest valuation exists and `totalOutstanding > 0`:  
     `valuePerShare = valuation / totalOutstanding`  
     stakeholder `value = shares * valuePerShare`.
   - Else: `value` is **`null`** for everyone (still compute shares and %).
5. **Ownership %:** `100 * shares / totalOutstanding` when total &gt; 0.

**Why this matches “splits don’t auto-scale future issuances”:**  
Future issuances are separate events **after** the split in the timeline; they add into the map in **post-split** units because the map already holds post-split counts.

---

## Phase 4: Edge cases (checklist)

| Case | Behavior |
|------|----------|
| No valuation before as-of | `value == null`; shares and % OK. |
| Invalid split / issuance / bad valuation | **Ignore** that event; valuation ignore **preserves** previous latest valuation. |
| `asOfDate` before any transaction | Empty cap table (or empty map). |
| Two valuations | **Last** valid one in **replay order** wins. |
| Same day, multiple of same type | Spec only orders **across** types; **stable sort** preserves input order among same type — call out in interview if asked. |

---

## Phase 5: Implementation order (timeboxed)

1. **POJOs** — `Transaction` + three types + `StakeholderCapTableRow` (15–20 min).
2. **Filter + sort + replay loop** — map only; no money yet (20–25 min).
3. **BigDecimal** for valuation, value per share, row value + ownership % (15–20 min).
4. **Unit tests** — one happy path, same-day order, no valuation, ignore invalid (10–15 min).

---

## Optional extensions (if time)

- **Stock split** as rational \(a:b\) instead of integer factor.
- **Multiple share classes** — nested map `ShareClass → (Stakeholder → shares)`.
- **Persistence** — append-only event log; replay from snapshot + tail.
