import {StrictMode} from 'react'
import ReactDOM from 'react-dom/client'
import {createRouter, RouterProvider} from '@tanstack/react-router'

import {routeTree} from './routeTree.gen'
import {QueryClientProvider} from "@tanstack/react-query";
import {queryClient} from "./lib/query.ts";
import {ReactQueryDevtools} from "@tanstack/react-query-devtools";

import './globals.css'

const router = createRouter({routeTree})

declare module '@tanstack/react-router' {
    interface Register {
        router: typeof router
    }
}

const rootElement = document.getElementById('root') as HTMLElement
if (!rootElement.innerHTML) {
    const root = ReactDOM.createRoot(rootElement)
    root.render(
        <StrictMode>
            <QueryClientProvider client={queryClient}>
                <RouterProvider router={router}/>
                <ReactQueryDevtools initialIsOpen={false}/>
            </QueryClientProvider>
        </StrictMode>,
    )
}