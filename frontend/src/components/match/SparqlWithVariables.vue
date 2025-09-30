<script setup lang="ts">
import type { ResultBinding, ResultBindingInfo } from "@/types/PatternInstance";
import { computed } from "vue";
import { splitBindingParts, valueToString } from "@/util/Utils";
import type { SparqlTokenInfo } from "@/types/SparqlTokenInfo";
import { getBindingColor } from "@/util/BindingColors";
import { mdiLabel } from "@mdi/js";

const props = defineProps<{
  sparql: string;
  bindings: ResultBinding[];
}>();

const KEYWORDS = [
  // Queries
  "SELECT",
  "ASK",
  "CONSTRUCT",
  "DESCRIBE",
  "WHERE",
  "GRAPH",
  "SERVICE",
  "VALUES",
  "UNION",
  "OPTIONAL",
  "MINUS",
  "FILTER",
  "BOUND",
  "EXISTS",
  "NOT",
  "GROUP",
  "BY",
  "HAVING",
  "ORDER",
  "LIMIT",
  "OFFSET",
  "DISTINCT",
  "REDUCED",
  "PREFIX",
  "BASE",
  "ASC",
  "DESC",

  // Updates
  "INSERT",
  "DELETE",
  "DATA",
  "WHERE",
  "LOAD",
  "CLEAR",
  "CREATE",
  "DROP",
  "COPY",
  "MOVE",
  "ADD",
  "WITH",
  "USING",
  "DEFAULT",
  "NAMED",
  "INTO",

  // Misc
  "TRUE",
  "FALSE",
  "A" // rdf:type shorthand
];

const PUNCTUATIONS = ["{", "}", "(", ")", ".", ",", ";"];

function isKeyword(token: string): boolean {
  return KEYWORDS.includes(token.toUpperCase());
}

function isBlankNode(token: string): boolean {
  return token.startsWith("_:");
}

function isPunctuation(token: string): boolean {
  return PUNCTUATIONS.includes(token);
}

/**
 * Splits the SPARQL into lines and individual tokens within each line. If the token corresponds to a new entity identifier,
 * mark its position in the line so that it can be replaced by a span with tooltip when rendering.
 * Lines not containing new entities are again joined into a single token to simplify the rendering.
 */
const tokensToRender = computed(() => {
  const lines = props.sparql.split("\n");
  const tokens: SparqlTokenInfo[][] = [];
  const bindingMap: Record<string, ResultBindingInfo> = {};

  for (const binding of props.bindings) {
    bindingMap[valueToString(binding)] = {
      ...binding,
      bindingParts: splitBindingParts(binding),
      bindingColor: getBindingColor(binding.name)
    };
  }

  for (let i = 0; i < lines.length; i++) {
    let leadingSpacesCount = 0;
    while (lines[i].charAt(leadingSpacesCount) === " ") {
      leadingSpacesCount++;
    }
    lines[i] = lines[i].trim();

    const tokenRegex = /([{}().,;]|[^\s{}().,;]+)/g; //remove whitespace, split words and punctiotions
    const lineTokens = [...lines[i].matchAll(tokenRegex)].map((match) => match[0]);

    //let lineTokens = lines[i].split(" ");
    if (leadingSpacesCount > 0) {
      lineTokens.unshift(" ".repeat(leadingSpacesCount - 1));
    }

    const tokenInfos: SparqlTokenInfo[] = lineTokens.map((token) => {
      if (bindingMap[token]) {
        return { text: token, type: "binding", bindingInfo: bindingMap[token] };
      } else if (isKeyword(token)) {
        return { text: token, type: "keyword" };
      } else if (isBlankNode(token)) {
        return { text: token, type: "blanknode" };
      } else if (isPunctuation(token)) {
        return { text: token, type: "punctuation" };
      } else {
        return { text: token, type: "other" };
      }
    });

    if (i < lines.length - 1) {
      tokenInfos.push({ text: "\n", type: "other" });
    }

    tokens.push(tokenInfos);
  }
  return tokens;
});
</script>

<template>
  <template v-for="(line, lineIndex) in tokensToRender" :key="lineIndex">
    <template v-for="(token, index) in line" :key="index">
      <template v-if="index > 0 && token.text !== '\n' && token.text !== '.'">{{ " " }}</template>

      <template v-if="token.type === 'binding'">
        <v-tooltip :text="token.bindingInfo?.name">
          <template v-if="token.bindingInfo" v-slot:activator="{ props }">
            <strong v-bind="props" class="text-grey-darken-3 text-decoration-hover-underline">
              <span>{{ token.bindingInfo.bindingParts.prefix }}{{ token.bindingInfo.bindingParts.base }}</span>
              <span :class="token.bindingInfo?.bindingColor">{{ token.bindingInfo.bindingParts.localName }}</span>
              <span>{{ token.bindingInfo.bindingParts.suffix }}</span>
            </strong>
          </template>

          <template v-else v-slot:activator="{ props }">
            <strong class="text-grey-darken-3" v-bind="props">{{ token.text }}</strong>
          </template>

          <div v-if="token.bindingInfo?.name">
            {{ token.bindingInfo.name }}
            <span class="d-flex align-center" v-if="token.bindingInfo.label">
              <v-icon size="small" :class="['mr-1', token.bindingInfo?.bindingColor]">{{ mdiLabel }}</v-icon>
              {{ token.bindingInfo.label }}
            </span>
          </div>
        </v-tooltip>
      </template>

      <template v-else-if="token.type === 'keyword' || token.type === 'punctuation'">
        <span class="text-blue-darken-3 font-weight-bold">{{ token.text }}</span>
      </template>

      <template v-else-if="token.type === 'blanknode'">
        <strong class="text-grey-darken-3">{{ token.text }}</strong>
      </template>

      <template v-else>
        {{ token.text }}
      </template>
    </template>
  </template>
</template>

<style>
.text-decoration-hover-underline:hover {
  text-decoration: underline;
}
</style>
