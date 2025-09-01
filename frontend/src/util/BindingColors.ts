const bindingColors = [
  "text-pink-darken-2",
  "text-blue",
  "text-orange",
  "text-teal",
  "text-lime-darken-1",
  "text-purple",
  "text-indigo",
  "text-cyan",
  "text-green-darken-2",
  "text-brown",
  "text-red-darken-4",
  "text-blue-darken-4",
  "text-teal-darken-4",
  "text-lime-darken-4",
  "text-orange-darken-4",
  "text-purple-darken-4",
  "text-indigo-darken-4",
  "text-cyan-darken-4",
  "text-green-darken-4",
  "text-brown-darken-4"
];

const bindingColorMap: Map<string, string> = new Map();

/**
 * Returns color class for the given binding name
 * @param bindingName : string
 * @return string
 */
export function getBindingColor(bindingName: string): string {
  if (!bindingColorMap.has(bindingName)) {
    bindingColorMap.set(bindingName, bindingColors[bindingColorMap.size % bindingColors.length]);
  }

  return bindingColorMap.get(bindingName)!;
}
