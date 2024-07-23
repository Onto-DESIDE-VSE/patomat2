export async function downloadAttachment(resp: Response) {
    const blob = await resp.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = resp.headers.get("content-disposition")!.split(";")[1].split("=")[1].replace(/"/g, "");
    a.click();
    window.URL.revokeObjectURL(url);
}
