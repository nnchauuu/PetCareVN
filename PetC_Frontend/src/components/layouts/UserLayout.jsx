import Header from './Header';
import Footer from './Footer';
import './Layout.css';

const UserLayout = ({ children }) => (
    <div className="user-layout">
        <Header />
        <main className="user-layout-content">
            {children}
        </main>
        <Footer />
    </div>
);

export default UserLayout;
