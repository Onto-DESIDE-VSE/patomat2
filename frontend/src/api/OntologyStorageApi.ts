import Constants from "@/constants/Constants";

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
