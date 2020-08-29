import React from "react";
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine-dark.css';

import Page from "./Page";
import ReadingView from "./views/ReadingView";
import SpendingView from "./views/SpendingView";
import useMediaQuery from "../hooks/useMediaQuery";
import {DarkModeContext} from "../contexts/DarkModeContext";

export default function MainPage({fullName, userName, logout}: { fullName: string, userName: string, logout: () => any }) {
  const isDarkMode = useMediaQuery("(prefers-color-scheme: dark)", false);
  return <Page header={<>
    <span>
      <span>Welcome </span>
      <span className="tooltip">{fullName}
        <div className={"tooltip-text header-inner header-inner-with-border"}>
          <a href={`https://github.com/${userName}`}
             target="_blank"
             rel="noopener noreferrer">GitHub: {userName}</a>
        </div>
      </span>
    </span>
    <button className="header-inner header-inner-with-border" onClick={logout}>Logout</button>
  </>} body={<DarkModeContext.Provider value={{isDarkMode}}>
    <ReadingView/>
    <SpendingView/>
  </DarkModeContext.Provider>}/>;
}