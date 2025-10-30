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

export async function uploadTransformationInput(ontology: File | string, patternFiles: File[], patternUrls: string[]) {
  const formData = new FormData();
  patternFiles.forEach((file) => formData.append("pattern", file));
  const data = { patterns: patternUrls } as any;
  if (typeof ontology === "string") {
    data.ontology = ontology;
  } else {
    formData.append("ontology", ontology);
  }
  formData.append("data", new Blob([JSON.stringify(data)], { type: Constants.MEDIA_TYPE_JSON }));
  return await fetch(`${Constants.SERVER_URL}/ontology`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    body: formData
  });
}

export async function clearSessionData() {
  return await fetch(`${Constants.SERVER_URL}/data`, {
    method: "DELETE",
    credentials: "include"
  });
}
