export type PatternInstance = {
  id: number;
  patternName: string;
  match: PatternMatch;
  sparqlInsert: string;
  sparqlDelete: string;
  newEntities: NewEntity[];
  status: boolean | null;
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
  basedOnBlankNode?: boolean;
  label?: string;
};

export type ResultBindingInfo = ResultBinding & {
  bindingParts: ResultBindingParts;
  bindingColor: string;
};

export type UriParts = {
  base: string;
  localName: string;
};

export type ResultBindingParts = {
  prefix: string; // eg. "<"
  base: string; // eg. "http://cmt/"
  localName: string; // eg. "assignReviewer"
  suffix: string; // eg. ">" nebo "^^datatype"
  basedOnBlankNode?: boolean;
};

export type NewEntity = {
  identifier: string;
  variableName: string;
  labels: EntityLabel[];
};

export type EntityLabel = {
  value: string;
  property: string;
  apply: boolean;
};
