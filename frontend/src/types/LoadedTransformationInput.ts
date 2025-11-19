export type LoadedTransformationInput = {
  ontology: string;
  patterns: PatternInfo[];
};

export type PatternInfo = {
  name: string;
  fileName: string;
};
