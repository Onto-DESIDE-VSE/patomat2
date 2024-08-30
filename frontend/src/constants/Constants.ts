import { mdiLabel, mdiLabelMultiple, mdiLabelOutline } from "@mdi/js";

/**
 * Aggregated object of process.env and window.__config__ to allow dynamic configuration
 */
const ENV = {
  ...Object.keys(import.meta.env).reduce<Record<string, string>>((acc, key) => {
    const strippedKey = key.replace("VITE_", "");
    acc[strippedKey] = import.meta.env[key]!;
    return acc;
  }, {}),
  ...(window as any).__config__
};

/**
 * Helper to make sure that all envs are defined properly
 * @param name env variable name (without the REACT_APP prefix)
 * @param defaultValue Default variable name
 */
export function getEnv(name: string, defaultValue?: string): string {
  const value = ENV[name] || defaultValue;
  if (value !== undefined) {
    return value;
  }
  throw new Error(`Missing environment variable: ${name}`);
}

const Constants = {
  APP_NAME: "PatOMat2",
  SERVER_URL: getEnv("SERVER_URL", ""),
  CONTEXT_PATH: getEnv("CONTEXT_PATH", ""),
  MESSAGE_TIMEOUT: 3000,

  LABEL_TYPES: {
    "http://www.w3.org/2000/01/rdf-schema#label": {
      property: "http://www.w3.org/2000/01/rdf-schema#label",
      propertyPrefixed: "rdfs:label",
      icon: mdiLabelOutline
    },
    "http://www.w3.org/2004/02/skos/core#prefLabel": {
      property: "http://www.w3.org/2004/02/skos/core#prefLabel",
      propertyPrefixed: "skos:prefLabel",
      icon: mdiLabel
    },
    "http://www.w3.org/2004/02/skos/core#altLabel": {
      property: "http://www.w3.org/2004/02/skos/core#altLabel",
      propertyPrefixed: "skos:altLabel",
      icon: mdiLabelMultiple
    }
  }
};

export default Constants;
