import Constants from "@/constants/Constants";
import type { NewEntityIdentifierConfig } from "@/types/NewEntityIdentifierConfig";

export async function getNewEntityIdentifierConfig(): Promise<NewEntityIdentifierConfig> {
  const resp = await fetch(`${Constants.SERVER_URL}/new-entity-identifier/config`, {
    credentials: "include"
  });
  if (resp.ok) {
    return resp.json();
  } else {
    throw new Error("Failed to get new entity identifier config");
  }
}

export async function setNewEntityIdentifierConfig(config: NewEntityIdentifierConfig) {
  const resp = await fetch(`${Constants.SERVER_URL}/new-entity-identifier/config`, {
    method: "PUT",
    body: JSON.stringify(config),
    credentials: "include",
    headers: {
      "Content-Type": "application/json"
    }
  });
  if (resp.ok) {
    return resp.json();
  } else {
    throw new Error("Failed to set new entity identifier config");
  }
}
