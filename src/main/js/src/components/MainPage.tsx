import React from "react";
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine.css';
import 'ag-grid-community/dist/styles/ag-theme-alpine-dark.css';

import Page from "./Page";
import ReadingTable from "./tables/ReadingTable";
import {DarkModeContext} from "../contexts/DarkModeContext";
import useMediaQuery from "../hooks/useMediaQuery";
import DailySpendingGroup from "./groups/DailySpendingGroup";
import MainContent from "./subcomponents/MainContent";
import WeeklySpendingGroup from "./groups/WeeklySpendingGroup";
import MonthlySpendingGroup from "./groups/MonthlySpendingGroup";

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
    <DailySpendingGroup/>
    <WeeklySpendingGroup/>
    <MonthlySpendingGroup/>
    <MainContent header={"Reading"} body={<ReadingTable/>}/>
  </DarkModeContext.Provider>}/>;
}