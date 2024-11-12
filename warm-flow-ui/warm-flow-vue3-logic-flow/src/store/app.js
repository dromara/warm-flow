import Cookies from 'js-cookie'
import {TokenKey, TokenName} from "@/utils/auth.js";
import {tokenName} from "@/api/anony.js";

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
        for (const [key, value] of urlParams.entries()) {
          if ("token" === key) {
            let res = await tokenName();
            Cookies.set(TokenName, res.data);
            Cookies.set(TokenKey, value)
          }
          params[key] = value;
        }
        this.appParams = params;
      }
    }
  })

export default useAppStore;
