import React, {useEffect, useState} from "react";

export default function useSavedState(defaultValue: string, key: string): [string, React.Dispatch<React.SetStateAction<string>>] {
  const [save, setSave] = useState(() => localStorage.getItem(key) || defaultValue);

  useEffect(() => localStorage.setItem(key, save), [save, key]);

  return [save, setSave];
}