export interface SecurityStats {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  onlineUsers: number;
  totalRoles: number;
  totalPermissions: number;
  failedLogins: number;
  lockedAccounts: number;
  activeSessions: number;
  userOverrides: number;
}

export interface LoginStats {
  successfulLogins: number;
  failedLogins: number;
  todayLogins: number;
  thisWeekLogins: number;
}
