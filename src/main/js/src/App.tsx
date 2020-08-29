import React, {useCallback, useEffect, useState} from 'react';
import './App.css';
import * as Cookie from "js-cookie";

import LoginPage from "./components/LoginPage";
import MainPage from "./components/MainPage";

function App() {

  const [authenticated, setAuthenticated] = useState(false as false | { fullName: string, userName: string });

  const getUser = useCallback(() => {
    fetch("/api/user")
    .then(response => response.text())
    .then(JSON.parse)
    .then(setAuthenticated)
    .catch(() => setAuthenticated(false));
  }, [setAuthenticated]);

  useEffect(getUser, [getUser])

  const logout = useCallback(() => {
    fetch("/logout", {
      redirect: "error",
      method: "POST",
      headers: {"X-XSRF-TOKEN": String(Cookie.get("XSRF-TOKEN"))}
    })
    .finally(getUser);
  }, [getUser]);


  return authenticated
      ? <MainPage
          userName={authenticated.userName}
          fullName={authenticated.fullName}
          logout={logout}/>
      : <LoginPage/>;
}

export default App;
