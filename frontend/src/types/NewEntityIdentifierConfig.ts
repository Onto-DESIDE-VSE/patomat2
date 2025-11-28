export const EntityLocalNameFormatOptions = [
  {
    name: "Random number",
    value: "RANDOM_NUMBER"
  },
  {
    name: "UUID",
    value: "UUID"
  },
  {
    name: "Label (camelCase)",
    value: "LABEL_CAMEL_CASE"
  },
  {
    name: "Label (PascalCase)",
    value: "LABEL_PASCAL_CASE"
  },
  {
    name: "Label (snake_case)",
    value: "LABEL_SNAKE_CASE"
  },
  {
    name: "Label (kebab-case)",
    value: "LABEL_KEBAB_CASE"
  }
];

export type EntityLocalNameFormat = (typeof EntityLocalNameFormatOptions)[0];

export type NewEntityIdentifierConfig = {
  localNameFormat: EntityLocalNameFormat;
  namespace: string;
};
