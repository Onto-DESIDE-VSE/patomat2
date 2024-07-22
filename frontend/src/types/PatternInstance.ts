export type PatternInstance = {
    id: number,
    patternName: string,
    match: PatternMatch,
};

export type PatternMatch = {
    id: number,
    patternFile: string,
    bindings: ResultBinding[]
};

export type ResultBinding = {
    name: string,
    value: string,
    datatype: string
};
