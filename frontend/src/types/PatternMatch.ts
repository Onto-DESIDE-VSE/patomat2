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
