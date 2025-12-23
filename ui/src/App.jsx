import {BrowserRouter, Route, Routes, Navigate} from "react-router-dom";
import Login from "./pages/Login.jsx";
import AuthGuard from "./components/AuthGuard.jsx";
import AdminLayout from "./components/AdminLayout.jsx";
import useUserStore from "./store/useUserStore.js";
import { lazy, Suspense } from "react";
const modules = import.meta.glob('./pages/**/*.jsx')

const Home = lazy(() => import('./pages/Home.jsx'))

const DynamicRoutes = () => {
    const { userInfo } = useUserStore()
    const menus = userInfo?.menus || []
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/home" replace />} />
            <Route path="/home" element={
                <Suspense fallback={<div>Loading...</div>}>
                    <Home />
                </Suspense>
            } />
            {menus.filter(a=>a.type == 2).map((menu) => {
                let importFn = modules[`./pages/${menu.component}.jsx`]
                if (!importFn) {
                    return null
                }
                const Component = lazy(importFn)
                return (
                    <Route
                        key={menu.id}
                        path={menu.path}
                        element={
                            <Suspense fallback={<div>Loading...</div>}>
                                <Component />
                            </Suspense>
                        }
                    />
                )
            })}
             <Route path="*" element={<div>Page not found</div>} />
        </Routes>
    )
}

function App() {
    return (
        <BrowserRouter basename={import.meta.env.VITE_BASE_ROUTE}>
            <Routes>
                <Route path={'/login'} element={<Login/>}/>
                <Route path={'/*'} element={
                    <AuthGuard>
                        <AdminLayout>
                            <DynamicRoutes />
                        </AdminLayout>
                    </AuthGuard>
                }/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
