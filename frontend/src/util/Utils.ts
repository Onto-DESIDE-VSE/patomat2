import Constants from "@/constants/Constants";
import type { ResultBinding, ResultBindingParts, UriParts } from "@/types/PatternInstance";

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

export function splitBindingParts(value: ResultBinding): ResultBindingParts {
  const uriParts = splitUriParts(value.value);

  if (value.datatype === Constants.RDFS_RESOURCE) {
    return {
      prefix: "<",
      base: uriParts.base,
      localName: uriParts.localName,
      suffix: ">",
      basedOnBlankNode: value.basedOnBlankNode
    };
  } else {
    return {
      prefix: "",
      base: "",
      localName: value.value,
      suffix: `^^${value.datatype}`,
      basedOnBlankNode: value.basedOnBlankNode
    };
  }
}

export function splitUriParts(uri: string): UriParts {
  if (!uri) {
    return { base: "", localName: "" };
  }

  const hashIndex = uri.indexOf("#");
  if (hashIndex !== -1) {
    return { base: uri.substring(0, hashIndex), localName: uri.substring(hashIndex) };
  }

  let slashIndex = uri.lastIndexOf("/");
  if (slashIndex === uri.length - 1) {
    slashIndex = uri.slice(0, -1).lastIndexOf("/");
  }

  if (slashIndex > 0) {
    return { base: uri.substring(0, slashIndex), localName: uri.substring(slashIndex) };
  } else {
    return { base: "", localName: uri };
  }
}
