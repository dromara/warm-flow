import Cookies from 'js-cookie'
import {TokenPrefix, TokenName} from "@/utils/auth.js";
import {tokenName} from "&/api/anony.js";
import {getTokenName} from "../utils/auth.js";

const useAppStore = defineStore(
  'app',
  {
    state: () => ({
      appParams: null
    }),
    actions: {
      async fetchTokenName() {
        const urlParams = new URLSearchParams(window.location.search);
        const params = {};
        let tokenNames = []
        await tokenName().then(res => {
            if (res.code === 200 && res.data && res.data.length > 0) {
                tokenNames = res.data
                Cookies.set(TokenName, res.data)
            }
        }).catch(() => {});
        this.appParams= null
        for (const [key, value] of urlParams.entries()) {
            if (tokenNames && tokenNames.length > 0 && tokenNames.includes(key)) {
                Cookies.set(TokenPrefix + key, value);
            }
            params[key] = value;
        }
        this.appParams = params;
      }
    }
  })

export default useAppStore;
