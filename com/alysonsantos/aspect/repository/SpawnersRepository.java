package com.alysonsantos.aspect.repository;

import com.alysonsantos.aspect.database.api.JdbcProvider;
import com.alysonsantos.aspect.database.api.KFunction;
import com.alysonsantos.aspect.models.Spawner;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.serialize.Position;

import java.sql.ResultSet;
import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class SpawnersRepository implements Repository<Position, Spawner> {

    private final JdbcProvider provider;
    private final Gson gson = GsonProvider.standard();

    @Override
    public KFunction<ResultSet, Spawner> getDeserializer() {
        return resultSet -> gson.fromJson(resultSet.getString("SPAWNER"), Spawner.class);
    }

    @Override
    public Collection<Spawner> selectAll() {
        return provider.queryCollection("SELECT * FROM aspect_spawners", getDeserializer());
    }

    @Override
    public Spawner selectOne(Position position) {
        return provider.query("SELECT * FROM aspect_spawners WHERE POSITION=?",
                getDeserializer(),
                position.serialize().toString()
        );
    }

    @Override
    public void insert(Position position, Spawner spawner) {
        provider.update(
                "INSERT INTO aspect_spawners (SPAWNER, POSITION) VALUES (?,?)",
                gson.toJson(spawner),
                position.serialize().toString()
        );
    }

    @Override
    public void update(Position position, Spawner spawner) {
        provider.update("UPDATE aspect_spawners SET SPAWNER=? WHERE POSITION=?",
                gson.toJson(spawner),
                position.serialize().toString()
        );
    }

    @Override
    public void delete(Position position) {
        provider.update("DELETE FROM aspect_spawners WHERE POSITION=?", position.serialize().toString());
    }
}
