import {setTokenName, setToken} from "@/utils/auth.js";
import {config} from "@/api/anony.js";

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
        await config().then(res => {
          if (res.code === 200 && res.data && res.data.tokenNameList && res.data.tokenNameList.length > 0) {
            tokenNames = res.data.tokenNameList
            setTokenName(res.data.tokenNameList)
          }
        }).catch(() => {});
        this.appParams= null
        for (const [key, value] of urlParams.entries()) {
          if (value && 'undefined' !== value) {
            if (tokenNames && tokenNames.length > 0 && tokenNames.includes(key)) {
              setToken(key, value);
            }
            params[key] = value;
          }
        }
        this.appParams = params;
      }
    }
  })

export default useAppStore;
