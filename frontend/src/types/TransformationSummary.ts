import type { NewEntity } from "@/types/PatternInstance";

export type TransformationSummary = {
  addedStatements: string;
  deletedStatements: string;
  addedEntities: NewEntity[];
};
