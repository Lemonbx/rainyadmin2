import useUserStore from "../store/useUserStore";

const Home = () => {
    const { userInfo, logout } = useUserStore()

    return (
        <div style={{ padding: 20 }}>
            <h1>Welcome, {userInfo?.nickname || userInfo?.username || 'User'}!</h1>
            <p>You are now logged in.</p>
            <button onClick={() => logout()}>Logout</button>
        </div>
    )
}

export default Home
