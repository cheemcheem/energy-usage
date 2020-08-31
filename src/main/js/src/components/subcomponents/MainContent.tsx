import React from "react";
import ErrorBoundary from "../../common/ErrorBoundary";
import {MainContentProps} from "../../common/Props";

export default function MainContent(props: MainContentProps) {
  const {body, header} = props;
  return <MainContentErrorBoundary>
    <div className="content content-rounded-border-box">
      <div className="content-header"><span>{header}</span></div>
      {body}
    </div>
  </MainContentErrorBoundary>
}

function MainContentErrorBoundary(props: React.PropsWithChildren<any>) {
  return <ErrorBoundary renderError={<MainContent header="Component Error"/>}>
    {props.children}
  </ErrorBoundary>
}