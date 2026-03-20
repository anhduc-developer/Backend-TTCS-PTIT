import { Navigate } from "react-router-dom";
import { useAppSelector } from "@/redux/hooks";
import NotPermitted from "./protected-route.ts/not-permitted";

interface IAdminRoute {
  children: React.ReactNode;
}

const AdminRoute = (props: IAdminRoute) => {
  const { children } = props;
  const user = useAppSelector((state) => state.account.user);
  const isAuthenticated = useAppSelector(
    (state) => state.account.isAuthenticated,
  );
  const isFetched = useAppSelector((state) => state.account.isFetched);

  if (!isFetched) return null; // chờ rehydrate/fetch xong

  if (!isAuthenticated) return <NotPermitted />; // chưa login

  const userRole = user?.role?.name ?? "";
  if (!["ADMIN", "HR"].includes(userRole)) return <NotPermitted />;

  return <>{children}</>;
};

export default AdminRoute;
