import { Navigate } from "react-router-dom";
import { useAppSelector } from "@/redux/hooks";
import NotPermitted from "./not-permitted";
import Loading from "../loading";

// Component kiểm tra role
const RoleBasedRoute = ({ allowedRoles, children }: any) => {
  const user = useAppSelector((state) => state.account.user);

  if (!user) return <Navigate to="/login" replace />; // Không có user => login

  const userRole = user.role.name;

  if (allowedRoles.includes(userRole)) {
    return <>{children}</>;
  } else {
    return <NotPermitted />; // Không có quyền => hiển thị trang forbidden
  }
};

// Component kiểm tra authentication và loading
const ProtectedRoute = ({ children }: any) => {
  const isAuthenticated = useAppSelector(
    (state) => state.account.isAuthenticated,
  );
  const isLoading = useAppSelector((state) => state.account.isLoading);

  if (isLoading) return <Loading />;

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  // Nếu đã login => kiểm tra role
  return (
    <RoleBasedRoute allowedRoles={["ADMIN", "HR"]}>{children}</RoleBasedRoute>
  );
};

export default ProtectedRoute;
