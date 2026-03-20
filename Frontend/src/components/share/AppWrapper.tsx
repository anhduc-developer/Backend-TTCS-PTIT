import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "@/redux/hooks";
import {
  setAuthFromLocalStorage,
  fetchAccount,
} from "@/redux/slice/accountSlide";
import Loading from "./loading";

interface AppWrapperProps {
  children: React.ReactNode;
}

const AppWrapper: React.FC<AppWrapperProps> = ({ children }) => {
  const dispatch = useAppDispatch();
  const { isLoading } = useAppSelector((state) => state.account);

  useEffect(() => {
    dispatch(setAuthFromLocalStorage());
    dispatch(fetchAccount()); // Nếu có refresh token, cập nhật lại user
  }, [dispatch]);

  if (isLoading) return <Loading />;

  return <>{children}</>;
};

export default AppWrapper;
