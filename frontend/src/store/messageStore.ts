import { defineStore } from "pinia"
import Constants from "@/constants/Constants"

const useMessageStore = defineStore("messages", {
  state: () => ({ messages: [] as string[] }),

  actions: {
    publishMessage(message: string) {
      this.messages.push(message)
      setTimeout(() => this.messages.splice(this.messages.indexOf(message), 1), Constants.MESSAGE_TIMEOUT)
    }
  }
})

export default useMessageStore
