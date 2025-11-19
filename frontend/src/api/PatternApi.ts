import Constants from "@/constants/Constants";
import type { PatternInfo } from "@/types/LoadedTransformationInput";

export async function getPredefinedPatterns(): Promise<PatternInfo[]> {
  const resp = await fetch(`${Constants.SERVER_URL}/patterns/predefined`, {
    method: "GET",
    credentials: "include"
  });
  if (resp.ok) {
    return await resp.json();
  } else {
    return [];
  }
}
