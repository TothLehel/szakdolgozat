ALTER TABLE [dates] ADD FOREIGN KEY ([driver_id]) REFERENCES [drivers] ([id])
GO

ALTER TABLE [schedule] ADD FOREIGN KEY ([driver_id]) REFERENCES [drivers] ([id])
GO

ALTER TABLE [vehicles] ADD FOREIGN KEY ([type]) REFERENCES [vehicle_type_limits] ([type])
GO

ALTER TABLE [stop_distances] ADD FOREIGN KEY ([start_stop_id]) REFERENCES [stops] ([id])
GO

ALTER TABLE [stop_distances] ADD FOREIGN KEY ([end_stop_id]) REFERENCES [stops] ([id])
GO

ALTER TABLE [schedule] ADD FOREIGN KEY ([license_plate]) REFERENCES [vehicles] ([license_plate])
GO

ALTER TABLE [schedule] ADD FOREIGN KEY ([route_id]) REFERENCES [routes] ([route_id])
GO

ALTER TABLE [route_destinations] ADD FOREIGN KEY ([route_id]) REFERENCES [routes] ([route_id])
GO

ALTER TABLE [stop_distances] ADD FOREIGN KEY ([traffic_id]) REFERENCES [traffic] ([id])
GO

ALTER TABLE [route_destinations] ADD FOREIGN KEY ([stop_distance_id]) REFERENCES [stop_distances] ([id])
GO

ALTER TABLE [stop_distances] ADD FOREIGN KEY ([roadId]) REFERENCES [road_types] ([roadId])

