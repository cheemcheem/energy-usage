import {useEffect, useState} from "react";

export default function useMediaQuery(query: string, defaultState: boolean) {
  const [queryState, setQueryState] = useState(defaultState);
  useEffect(() => {
    /**
     * Update queryState when detects CSS media queries changed.
     */
    const switchState = () => setQueryState(!queryState);
    const mediaQuery = window.matchMedia(query);

    setQueryState(mediaQuery.matches);

    // use deprecated api if current api is not supported
    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener("change", switchState);
    } else {
      // noinspection JSDeprecatedSymbols
      mediaQuery.addListener(switchState);
    }

    return () => {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener("change", switchState);
      } else {
        // noinspection JSDeprecatedSymbols
        mediaQuery.removeListener(switchState);
      }
    }
  }, [queryState, query])
  return queryState;
}