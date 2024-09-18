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

export async function uploadTransformationInputFiles(ontology: File, patterns: File[]) {
  const formData = new FormData();
  formData.append("ontology", ontology);
  patterns.forEach((file) => formData.append("pattern", file));
  return await fetch(`${Constants.SERVER_URL}/ontology/files`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    body: formData
  });
}

export async function uploadTransformationInputUrls(ontology: string, patterns: string[]) {
  return await fetch(`${Constants.SERVER_URL}/ontology/urls`, {
    credentials: Constants.SERVER_URL.length > 0 ? "include" : "same-origin",
    method: "POST",
    headers: {
      "Content-Type": Constants.MEDIA_TYPE_JSON
    },
    body: JSON.stringify({
      ontology,
      patterns
    })
  });
}
