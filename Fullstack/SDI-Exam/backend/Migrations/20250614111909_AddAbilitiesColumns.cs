using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace backend.Migrations
{
    /// <inheritdoc />
    public partial class AddAbilitiesColumns : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Characters_Abilities_AbilitatiId",
                table: "Characters");

            migrationBuilder.DropTable(
                name: "Abilities");

            migrationBuilder.DropIndex(
                name: "IX_Characters_AbilitatiId",
                table: "Characters");

            migrationBuilder.DropColumn(
                name: "AbilitatiId",
                table: "Characters");

            migrationBuilder.AddColumn<float>(
                name: "Armor",
                table: "Characters",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.AddColumn<float>(
                name: "Health",
                table: "Characters",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.AddColumn<float>(
                name: "Mana",
                table: "Characters",
                type: "real",
                nullable: false,
                defaultValue: 0f);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Armor",
                table: "Characters");

            migrationBuilder.DropColumn(
                name: "Health",
                table: "Characters");

            migrationBuilder.DropColumn(
                name: "Mana",
                table: "Characters");

            migrationBuilder.AddColumn<int>(
                name: "AbilitatiId",
                table: "Characters",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateTable(
                name: "Abilities",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Armor = table.Column<float>(type: "real", nullable: false),
                    Health = table.Column<float>(type: "real", nullable: false),
                    Mana = table.Column<float>(type: "real", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Abilities", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Characters_AbilitatiId",
                table: "Characters",
                column: "AbilitatiId");

            migrationBuilder.AddForeignKey(
                name: "FK_Characters_Abilities_AbilitatiId",
                table: "Characters",
                column: "AbilitatiId",
                principalTable: "Abilities",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
