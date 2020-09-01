import React from "react";
import ErrorBoundary from "../../common/ErrorBoundary";
import {BaseContentProps} from "../../common/Props";

export default function ContentBody(props: BaseContentProps) {
  const {body, extraBodyClass} = props;
  return <ContentErrorBoundary>
    <div className={`content-body ${extraBodyClass}`}>
      {body}
    </div>
  </ContentErrorBoundary>
}

function ContentErrorBoundary(props: React.PropsWithChildren<any>) {
  return <ErrorBoundary renderError={<ContentBody/>}>
    {props.children}
  </ErrorBoundary>
}