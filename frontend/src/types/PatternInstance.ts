export type PatternInstance = {
  id: number;
  patternName: string;
  match: PatternMatch;
  sparqlInsert: string;
  sparqlDelete: string;
  newEntities: NewEntity[];
};

export type PatternMatch = {
  id: number;
  patternFile: string;
  bindings: ResultBinding[];
};

export type ResultBinding = {
  name: string;
  value: string;
  datatype: string;
};

export type NewEntity = {
  identifier: string;
  variableName: string;
  label: string;
};
