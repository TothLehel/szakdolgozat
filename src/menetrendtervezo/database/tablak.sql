CREATE TABLE [drivers] (
  [id] int PRIMARY KEY,
  [last_name] varchar(255),
  [for_name] varchar(255)
)
GO

CREATE TABLE [vehicles] (
  [license_plate] varchar(7) PRIMARY KEY,
  [name] varchar(255),
  [type] varchar(2)
)
GO

CREATE TABLE [vehicle_type_limits] (
  [type] varchar(2) PRIMARY KEY,
  [speedway_speed_limit] int,
  [highway_speed_limit] int,
  [non_residental_area_limit] int,
  [residental_speed_limit] int
)
GO

CREATE TABLE [stops] (
  [id] int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  [name] varchar(255),
  [capacity] int
)
GO

CREATE TABLE [schedule] (
  [schedule_id] int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  [schedule_name] varchar(50),
  [driver_id] int,
  [start_date] timestamp,
  [end_date] timestamp,
  [license_plate] varchar(7),
  [route_id] int,
  [app_group] varchar(10)
)
GO

CREATE TABLE [routes] (
  [route_id] int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  [route_name] varchar(255)
)
GO

CREATE TABLE [dates] (
  [driver_id] int,
  [date] date,
  [start_time] timestamp,
  [end_time] timestamp,
  [pause_time] time,
  PRIMARY KEY ([driver_id], [date])
)
GO

CREATE TABLE [stop_distances] (
  [id] int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  [start_stop_id] int,
  [end_stop_id] int,
  [distance] double,
  [roadId] varchar(30),
  [traffic_id] int
)
GO

CREATE TABLE [traffic] (
  [id] int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  [day_name] varchar(255),
  [start_time] time,
  [end_time] time,
  [speed_modifier] int
)
GO

CREATE TABLE [route_destinations] (
  [route_id] int,
  [number] int,
  [stop_distance_id] int,
  PRIMARY KEY ([route_id], [number])
)
GO

CREATE TABLE [road_types] (
  [roadId] varchar (30) PRIMARY KEY,
  [road_type] varchar (30)
)
GO

CREATE TABLE [route_table_view] (
  [route_id] int PRIMARY KEY,
  [route_name] varchar(255),
  [start_name] varchar(255),
  [end_name] varchar(255),
  [distance] double,
  [num_of_stops] int
)