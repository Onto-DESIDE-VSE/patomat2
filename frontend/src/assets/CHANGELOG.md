# Changelog

---

<br/>

### 2025-11-19

- Support specifying predefined patterns for the user to select from.

### 2025-10-30

- Support explicitly clearing user's data (instead of waiting for automatic cleanup after session expiration).

### 2025-10-26

- Support sorting pattern instances using a LLM.

### 2025-10-10

- Include information about new entities in transformation summary.

### 2024-09-26

- Support combining files and URLs as transformation input.

### 2024-09-18

- Support to apply transformation immediately on the (up)loaded transformation input.

### 2024-09-17

- Support blank nodes in pattern match query results when they represent a `owl:unionOf`.

### 2024-09-11

- Allow providing examples of transformation input and using these examples.

### 2024-09-06

- Allow loading ontology and patterns from URL.

### 2024-08-30

- Allow selecting the label property and choosing whether a label is applied or not.

### 2024-08-27

- Implemented pattern match statistics and filtering of matches by pattern.

### 2024-08-14

- Implemented name transformation functions `head_noun`, `passivize` and `nominalize`.

### 2024-08-13

- Support filters in transformation patterns.

### 2024-08-08

- Show transformation summary on successful transformation.

### 2024-08-06

- Support the `label` function in name transformation patterns. PatOMat2 no longer automatically attempts to extract
  resource label, the `label` function must be explicitly used for this.

### 2024-08-05

- Modify pattern parser to work with the new name transformation structure (no more arrays)

### 2024-07-30

- Added support for editing new entity label
