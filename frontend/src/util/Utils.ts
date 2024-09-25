import Constants from "@/constants/Constants";

export async function downloadAttachment(resp: Response) {
  const blob = await resp.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = resp.headers.get("content-disposition")!.split(";")[1].split("=")[1].replace(/"/g, "");
  a.click();
  window.URL.revokeObjectURL(url);
}

export function valueToString(value: { value: string; datatype: string }): string {
  if (value.datatype === Constants.RDFS_RESOURCE) {
    return `<${value.value}>`;
  } else {
    return `${value.value}^^${value.datatype}`;
  }
}
