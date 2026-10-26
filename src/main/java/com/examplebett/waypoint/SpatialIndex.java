package com.examplebett.waypoint;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class SpatialIndex {
    private static final int CELL_SIZE = 64;

    private final Map<Long, Set<Integer>> cells = new HashMap<>();
    private final Map<Integer, Long> idToCellKey = new HashMap<>();

    private static long cellKey(int cellX, int cellZ) {
        return ((long) cellX << 32) | (cellZ & 0xFFFFFFFFL);
    }

    private static int cellCoord(int blockCoord) {
        return Math.floorDiv(blockCoord, CELL_SIZE);
    }

    void add(int id, BlockPos pos) {
        long key = cellKey(cellCoord(pos.getX()), cellCoord(pos.getZ()));
        this.cells.computeIfAbsent(key, k -> new HashSet<>()).add(id);
        this.idToCellKey.put(id, key);
    }

    void remove(int id) {
        Long key = this.idToCellKey.remove(id);
        if (key == null) {
            return;
        }
        Set<Integer> cell = this.cells.get(key);
        if (cell != null) {
            cell.remove(id);
            if (cell.isEmpty()) {
                this.cells.remove(key);
            }
        }
    }

    void update(int id, BlockPos pos) {
        long newKey = cellKey(cellCoord(pos.getX()), cellCoord(pos.getZ()));
        Long oldKey = this.idToCellKey.get(id);
        if (oldKey != null && oldKey == newKey) {
            return;
        }
        if (oldKey != null) {
            Set<Integer> oldCell = this.cells.get(oldKey);
            if (oldCell != null) {
                oldCell.remove(id);
                if (oldCell.isEmpty()) {
                    this.cells.remove(oldKey);
                }
            }
        }
        this.cells.computeIfAbsent(newKey, k -> new HashSet<>()).add(id);
        this.idToCellKey.put(id, newKey);
    }

    List<Integer> candidatesNear(BlockPos center, double radius) {
        int minCellX = cellCoord((int) Math.floor(center.getX() - radius));
        int maxCellX = cellCoord((int) Math.floor(center.getX() + radius));
        int minCellZ = cellCoord((int) Math.floor(center.getZ() - radius));
        int maxCellZ = cellCoord((int) Math.floor(center.getZ() + radius));

        List<Integer> result = new ArrayList<>();
        for (int cx = minCellX; cx <= maxCellX; cx++) {
            for (int cz = minCellZ; cz <= maxCellZ; cz++) {
                Set<Integer> cell = this.cells.get(cellKey(cx, cz));
                if (cell != null) {
                    result.addAll(cell);
                }
            }
        }
        return result;
    }
}
