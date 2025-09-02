import type { ResultBindingInfo } from "@/types/PatternInstance";

export type SparqlTokenInfo = {
  text: string;
  type: "binding" | "keyword" | "blanknode" | "punctuation" | "other";
  bindingInfo?: ResultBindingInfo;
};
