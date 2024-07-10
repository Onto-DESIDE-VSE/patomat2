/**
 * Aggregated object of process.env and window.__config__ to allow dynamic configuration
 */
const ENV = {
    ...Object.keys(import.meta.env).reduce<Record<string, string>>((acc, key) => {
        const strippedKey = key.replace("VITE_", "");
        acc[strippedKey] = import.meta.env[key]!;
        return acc;
    }, {}),
    ...(window as any).__config__,
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
};

export default Constants;
