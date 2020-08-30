import React from 'react';
import {render} from '@testing-library/react';
import App from './App';

test('renders log in page', () => {
  const {getByText} = render(<App/>);
  const linkElement = getByText(/Please log in to continue/i);
  expect(linkElement).toBeInTheDocument();
});
