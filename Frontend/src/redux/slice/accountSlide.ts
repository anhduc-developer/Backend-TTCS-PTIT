import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { callFetchAccount } from "@/config/api";

/* ================= TYPES ================= */

export interface IPermission {
  id: string;
  name: string;
  apiPath: string;
  method: string;
  module: string;
}

export interface IRole {
  id?: string;
  name?: string;
  permissions?: IPermission[];
}

export interface IUser {
  id: string;
  email: string;
  name: string;
  gender?: string;
  address?: string;
  age?: number;
  role: IRole;
}

interface IState {
  isAuthenticated: boolean;
  isLoading: boolean;
  isRefreshToken: boolean;
  errorRefreshToken: string;
  user: IUser;
  activeMenu: string;
  isFetched: boolean; // để track xem đã load account từ API/localStorage chưa
}

/* ================= DEFAULT USER ================= */

const defaultUser: IUser = {
  id: "",
  email: "",
  name: "",
  gender: undefined,
  address: undefined,
  age: undefined,
  role: {
    id: "",
    name: "",
    permissions: [],
  },
};

/* ================= THUNK ================= */

export const fetchAccount = createAsyncThunk(
  "account/fetchAccount",
  async () => {
    const response = await callFetchAccount();
    return response?.data ?? {};
  },
);

/* ================= INITIAL STATE ================= */

const initialState: IState = {
  isAuthenticated: false,
  isLoading: true,
  isRefreshToken: false,
  errorRefreshToken: "",
  user: defaultUser,
  activeMenu: "home",
  isFetched: false,
};

/* ================= SLICE ================= */

export const accountSlide = createSlice({
  name: "account",
  initialState,
  reducers: {
    setActiveMenu: (state, action: PayloadAction<string>) => {
      state.activeMenu = action.payload;
    },

    setUserLoginInfo: (state, action: PayloadAction<IUser>) => {
      state.isAuthenticated = true;
      state.isLoading = false;
      state.user = { ...defaultUser, ...action.payload };
      state.isFetched = true;

      // Persist user + auth
      localStorage.setItem("user", JSON.stringify(state.user));
      localStorage.setItem("isAuthenticated", "true");
    },

    setLogoutAction: (state) => {
      state.isAuthenticated = false;
      state.user = defaultUser;
      state.isFetched = true;

      localStorage.removeItem("access_token");
      localStorage.removeItem("refresh_token");
      localStorage.removeItem("user");
      localStorage.removeItem("isAuthenticated");
    },

    setRefreshTokenAction: (
      state,
      action: PayloadAction<{ status?: boolean; message?: string }>,
    ) => {
      state.isRefreshToken = action.payload?.status ?? false;
      state.errorRefreshToken = action.payload?.message ?? "";
    },

    // Rehydrate state từ localStorage
    setAuthFromLocalStorage: (state) => {
      const user = localStorage.getItem("user");
      const auth = localStorage.getItem("isAuthenticated");

      if (user && auth === "true") {
        state.user = JSON.parse(user);
        state.isAuthenticated = true;
      }
      state.isFetched = true;
      state.isLoading = false;
    },

    setUserProfile: (state, action: PayloadAction<Partial<IUser>>) => {
      state.user = { ...state.user, ...action.payload };
      localStorage.setItem("user", JSON.stringify(state.user));
    },
  },

  extraReducers: (builder) => {
    builder.addCase(fetchAccount.pending, (state) => {
      state.isLoading = true;
    });

    builder.addCase(fetchAccount.fulfilled, (state, action) => {
      state.isAuthenticated = true;
      state.isLoading = false;

      const payload = action.payload ?? {};
      const userData = (payload as any).user ?? payload;

      state.user = { ...defaultUser, ...(userData as IUser) };
      state.isFetched = true;

      // Persist
      localStorage.setItem("user", JSON.stringify(state.user));
      localStorage.setItem("isAuthenticated", "true");
    });

    builder.addCase(fetchAccount.rejected, (state) => {
      state.isAuthenticated = false;
      state.isLoading = false;
      state.isFetched = true;

      localStorage.removeItem("user");
      localStorage.removeItem("isAuthenticated");
    });
  },
});

/* ================= EXPORT ================= */

export const {
  setActiveMenu,
  setUserLoginInfo,
  setLogoutAction,
  setRefreshTokenAction,
  setAuthFromLocalStorage,
  setUserProfile,
} = accountSlide.actions;

export default accountSlide.reducer;
