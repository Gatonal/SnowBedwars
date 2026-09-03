package com.roberto.snow.arena;

public enum ArenaMode {
    SOLO(2, 1), DOUBLES(2, 2), THREES(4, 3), FOURS(4, 4);
    private final int teams, playersPerTeam;
    ArenaMode(int teams, int playersPerTeam) { this.teams = teams; this.playersPerTeam = playersPerTeam; }
    public int getTeams() { return teams; }
    public int getPlayersPerTeam() { return playersPerTeam; }
    public int getMaxPlayers() { return teams * playersPerTeam; }
    public static ArenaMode parse(String input) {
        String value = input.toLowerCase().replace("-", "");
        if ("solo".equals(value) || "1v1".equals(value)) return SOLO;
        if ("doubles".equals(value) || "2v2".equals(value)) return DOUBLES;
        if ("3v3v3v3".equals(value) || "threes".equals(value)) return THREES;
        if ("4v4v4v4".equals(value) || "fours".equals(value)) return FOURS;
        return null;
    }
}
