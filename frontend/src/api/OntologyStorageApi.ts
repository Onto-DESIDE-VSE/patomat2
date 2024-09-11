import Constants from "@/constants/Constants";
import { downloadAttachment } from "@/util/Utils";

export async function getLoadedInput() {
  const resp = await fetch(`${Constants.SERVER_URL}/ontology`, {
    method: "GET",
    credentials: "include"
  });
  if (resp.ok) {
    return await resp.json();
  } else if (resp.status === 404) {
    return null;
  }
}

export async function downloadOntologyFile() {
  const resp = await fetch(`${Constants.SERVER_URL}/ontology/content`, {
    method: "GET",
    credentials: "include"
  });
  await downloadAttachment(resp);
}
