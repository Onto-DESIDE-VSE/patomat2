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

const SKOS_PREF_LABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";
const SKOS_ALT_LABEL = "http://www.w3.org/2004/02/skos/core#altLabel";
const RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
const RDFS_RESOURCE = "http://www.w3.org/2000/01/rdf-schema#Resource";

const LABEL_TYPES: { [key: string]: { property: string; propertyPrefixed: string; icon: string } } = {};
LABEL_TYPES[RDFS_LABEL] = {
  property: RDFS_LABEL,
  propertyPrefixed: "rdfs:label",
  icon: mdiLabelOutline
};
LABEL_TYPES[SKOS_PREF_LABEL] = {
  property: SKOS_PREF_LABEL,
  propertyPrefixed: "skos:prefLabel",
  icon: mdiLabel
};
LABEL_TYPES[SKOS_ALT_LABEL] = {
  property: SKOS_ALT_LABEL,
  propertyPrefixed: "skos:altLabel",
  icon: mdiLabelMultiple
};

const Constants = {
  APP_NAME: "PatOMat2",
  SERVER_URL: getEnv("SERVER_URL", ""),
  CONTEXT_PATH: getEnv("CONTEXT_PATH", ""),
  MESSAGE_TIMEOUT: 3000,
  MEDIA_TYPE_JSON: "application/json",
  SKOS_PREF_LABEL,
  SKOS_ALT_LABEL,
  RDFS_LABEL,
  RDFS_RESOURCE,

  LABEL_TYPES
};

export default Constants;
