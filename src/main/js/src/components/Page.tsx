import React from "react";
import './Page.css';

export default function Page({header, body}: { header: React.ReactNode, body: React.ReactNode }) {
  return <>
    <div id="app">
      <header>
        {header}
      </header>
      <main>
        {body}
      </main>
    </div>
  </>;
}