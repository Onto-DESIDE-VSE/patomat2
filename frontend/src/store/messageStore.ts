import {defineStore} from "pinia";

const useMessageStore = defineStore("messages", {
    state:() => ({messages: [] as string[]}),

    actions: {

        publishMessage(message: string) {
            this.messages.push(message);
            setTimeout(() => this.messages.splice(this.messages.indexOf(message), 1), 5000);
        }
    }
});

export default useMessageStore;
