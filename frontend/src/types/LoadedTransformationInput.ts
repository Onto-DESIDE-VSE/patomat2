export type LoadedTransformationInput = {
  ontology: string;
  patterns: PatternInfo[];
};

declare type PatternInfo = {
  name: string;
  fileName: string;
};
